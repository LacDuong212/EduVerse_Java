import { useState, useEffect, useRef } from "react";

export const useStep4 = (stepperInstance, draftData, onSave, onSubmit, isSubmitting) => {
  // init
  const [tagsInput, setTagsInput] = useState(draftData.tags?.join(', ') || '');
  const isFirstRender = useRef(true);

  // auto-save tags with debounce
  useEffect(() => {
    // skip the very first render to avoid overwriting parent state immediately
    if (isFirstRender.current) {
      isFirstRender.current = false;
      return;
    }

    // wait before saving
    const timeoutId = setTimeout(() => {
      const formattedTags = tagsInput
        .split(',')
        .map((t) => t.trim().toLowerCase()) // clean whitespace & lowercase
        .filter((t) => t.length > 0)        // remove empty strings
        .slice(0, 14);                      // limit to 14 tags

      onSave({ tags: formattedTags });
    }, 500);

    // cleanup timeout if user types again quickly
    return () => clearTimeout(timeoutId);
  }, [tagsInput, onSave]);

  // --- handlers ---
  const handleTagsChange = (e) => {
    setTagsInput(e.target.value);
  };

  const goBack = () => {
    stepperInstance?.previous();
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (isSubmitting) return;
    onSubmit();
  };

  return {
    state: { tagsInput, isSubmitting },
    handlers: { handleTagsChange, goBack, handleSubmit }
  };
};