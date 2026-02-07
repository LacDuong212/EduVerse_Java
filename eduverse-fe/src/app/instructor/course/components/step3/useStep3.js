import { useState, useEffect } from "react";
import axios from "axios";
import { toast } from "react-toastify";

const backendUrl = import.meta.env.VITE_BACKEND_URL;

export const useStep3 = (stepperInstance, draftData, onSave) => {
  const [curriculum, setCurriculum] = useState([]);
  const [errors, setErrors] = useState({});

  // section modal
  const [showSectionModal, setShowSectionModal] = useState(false);
  const [editingSection, setEditingSection] = useState(null);
  const [editingSectionIndex, setEditingSectionIndex] = useState(null);

  // lecture modal
  const [showLectureModal, setShowLectureModal] = useState(false);
  const [editingLecture, setEditingLecture] = useState(null);
  const [editingLectureIndex, setEditingLectureIndex] = useState(null);
  const [activeSectionIndex, setActiveSectionIndex] = useState(null);

  // AI modal
  const [showAIModal, setShowAIModal] = useState(false);
  const [selectedAILecture, setSelectedAILecture] = useState(null);

  // initialize curriculum
  useEffect(() => {
    const data = draftData || {};
    setCurriculum(
      (data.curriculum || []).map(section => ({
        ...section,
        lectures: section.lectures || [],
      }))
    );
  }, [JSON.stringify(draftData?.curriculum)]);

  // computed stats
  const totalSections = curriculum.length;
  const totalLectures = curriculum.reduce((acc, sec) => acc + (sec.lectures?.length || 0), 0);

  // --- SECTION handlers ---
  const openAddSection = () => {
    setEditingSection(null);
    setEditingSectionIndex(null);
    setShowSectionModal(true);
  };

  const openEditSection = (index, sectionData) => {
    setEditingSection({ title: sectionData.section });
    setEditingSectionIndex(index);
    setShowSectionModal(true);
  };

  const handleSaveSection = (title) => {
    setCurriculum(prev => {
      const updated = [...prev];
      if (editingSectionIndex !== null) {
        // edit existing
        updated[editingSectionIndex] = { ...updated[editingSectionIndex], section: title };
      } else {
        // add new
        updated.push({ section: title, lectures: [] });
      }
      return updated;
    });
    if (errors.curriculum) setErrors(p => ({ ...p, curriculum: null }));
    setShowSectionModal(false);
  };

  const handleRemoveSection = (index) => {
    if (!window.confirm("Are you sure? All lectures in this section will be deleted.")) return;
    setCurriculum(prev => prev.filter((_, i) => i !== index));
  };

  // --- LECTURE handlers ---
  const openAddLecture = (sectionIndex) => {
    setEditingLecture(null);
    setEditingLectureIndex(null);
    setActiveSectionIndex(sectionIndex);
    setShowLectureModal(true);
  };

  const openEditLecture = (sectionIndex, lectureIndex, lectureData) => {
    setEditingLecture(lectureData);
    setEditingLectureIndex(lectureIndex);
    setActiveSectionIndex(sectionIndex);
    setShowLectureModal(true);
  };

  const handleSaveLecture = (lectureData) => {
    let lectureToSave = { ...lectureData };
    if (lectureData.videoFile) {
      const tempUrl = URL.createObjectURL(lectureData.videoFile);
      lectureToSave.videoUrl = tempUrl;
      lectureToSave.videoFile = null;
    }

    setCurriculum(prev => {
      const updated = [...prev];
      const targetSection = { ...updated[activeSectionIndex] };
      const targetLectures = [...targetSection.lectures];

      if (editingLectureIndex !== null) {
        // edit
        targetLectures[editingLectureIndex] = lectureToSave;
      } else {
        // add
        targetLectures.push(lectureToSave);
      }

      targetSection.lectures = targetLectures;
      updated[activeSectionIndex] = targetSection;
      return updated;
    });

    setShowLectureModal(false);
  };

  const handleRemoveLecture = (sectionIndex, lectureIndex) => {
    if (!window.confirm("Remove this lecture?")) return;
    setCurriculum(prev => {
      const updated = [...prev];
      updated[sectionIndex].lectures.splice(lectureIndex, 1);
      return updated;
    });
  };

  // --- AI logic ---
  const openAIModal = (sectionIdx, lectureIdx) => {
    const lecture = curriculum[sectionIdx].lectures[lectureIdx];
    setSelectedAILecture({ sectionIdx, lectureIdx, data: lecture });
    setShowAIModal(true);
  };

  const handleGenerateAI = async () => {
    if (!selectedAILecture) return;
    const { sectionIdx, lectureIdx, data: lecture } = selectedAILecture;

    if (!draftData?.id || !lecture.id) {
      toast.warning("Please save changes first.");
      return;
    }
    if (!lecture.videoUrl) {
      toast.warning("Video missing.");
      return;
    }

    try {
      // update status to processing
      updateLectureAIStatus(sectionIdx, lectureIdx, "Processing");
      
      setSelectedAILecture(prev => ({ 
         ...prev, 
         data: { ...prev.data, aiData: { status: "Processing" } } 
      }));

      const { data } = await axios.post(
        `${backendUrl}/api/courses/generate-ai`,
        { courseId: draftData.id, lectureId: lecture.id, videoKey: lecture.videoUrl },
        { withCredentials: true }
      );

      if (!data.success) throw new Error(data.message);
      toast.info("AI Generation started...");
    } catch (error) {
      console.error(error);
      toast.error("Generation failed");
      updateLectureAIStatus(sectionIdx, lectureIdx, "Failed");

      // update state to failed
      setSelectedAILecture(prev => ({ 
         ...prev, 
         data: { ...prev.data, aiData: { status: "Failed" } } 
      }));
    }
  };

  const handleDeleteAI = () => {
    if (!selectedAILecture || !window.confirm("Delete this AI content?")) return;
    const { sectionIdx, lectureIdx } = selectedAILecture;

    setCurriculum(prev => {
       const updated = [...prev];
       const lec = updated[sectionIdx].lectures[lectureIdx];
       lec.aiData = null; // clear data
       return updated;
    });

    // close modal or update it to empty state
    setShowAIModal(false);
    toast.success("AI Content removed.");
  };

  // helper to update nested state
  const updateLectureAIStatus = (sectionIdx, lectureIdx, status) => {
    setCurriculum(prev => {
      const updated = [...prev];
      const lec = updated[sectionIdx].lectures[lectureIdx];
      if (!lec.aiData) lec.aiData = {};
      lec.aiData.status = status;
      return updated;
    });
  };

  // AI Polling
  useEffect(() => {
    const hasProcessing = curriculum.some(s => s.lectures?.some(l => l.aiData?.status === "Processing"));
    if (!hasProcessing || !draftData?.id) return;

    const interval = setInterval(async () => {
      try {
        const { data } = await axios.get(`${backendUrl}/api/instructor/courses/${draftData.id}`, { withCredentials: true });
        if (data.success && data.course) {

          setCurriculum(prev => prev.map((sec) => {
            const serverSection = data.course.curriculum.find(s => s.section === sec.section);
            if (!serverSection) return sec;

            return {
              ...sec,
              lectures: sec.lectures.map(lec => {
                const serverLec = serverSection.lectures?.find(l => l.id === lec.id);
                if (serverLec && serverLec.aiData?.status !== lec.aiData?.status) {
                  return { ...lec, aiData: serverLec.aiData };
                }
                return lec;
              })
            };
          }));

          if (showAIModal && selectedAILecture) {
            // #TODO: Update selectedAILecture data if changed
          }
        }
      } catch (e) { console.error("Polling error", e); }
    }, 10000);

    return () => clearInterval(interval);
  }, [curriculum, draftData?.id]);

  // auto-save
  useEffect(() => {
    const handler = setTimeout(() => {
      onSave({ curriculum, lecturesCount: totalLectures });
    }, 2000);
    return () => clearTimeout(handler);
  }, [curriculum, totalLectures, onSave]);

  const validate = () => {
    const errs = {};
    if (curriculum.length === 0) errs.curriculum = "Add at least one section.";
    else if (curriculum.some(s => s.lectures.length === 0)) errs.curriculum = "Sections must have lectures.";
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // if (!validate()) {
    //   toast.error("Check errors.");
    //   return;
    // }
    onSave({ curriculum, lecturesCount: totalLectures });
    toast.success("Step 3 saved!");
    stepperInstance?.next();
  };

  return {
    data: { curriculum, errors, stats: { totalSections, totalLectures }, courseId: draftData?.id },
    modals: {
      section: { show: showSectionModal, data: editingSection, close: () => setShowSectionModal(false) },
      lecture: { show: showLectureModal, data: editingLecture, close: () => setShowLectureModal(false) },
      ai: { 
        show: showAIModal, 
        data: (selectedAILecture && curriculum[selectedAILecture.sectionIdx]?.lectures[selectedAILecture.lectureIdx]) || null, 
        close: () => setShowAIModal(false) 
      }
    },
    handlers: {
      openAddSection, openEditSection, handleSaveSection, handleRemoveSection,
      openAddLecture, openEditLecture, handleSaveLecture, handleRemoveLecture,
      openAIModal, handleGenerateAI, handleDeleteAI,
      handleSubmit, goBack: (e) => { e.preventDefault(); stepperInstance?.previous(); }
    }
  };
};