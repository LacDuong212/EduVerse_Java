import axios from "axios";
import { useCallback, useEffect, useState } from "react";

export default function useInstructorMyCourses(initialParams = {}) {
  const backendUrl = import.meta.env.VITE_BACKEND_URL;

  const [courses, setCourses] = useState([]);
  const [pagination, setPagination] = useState({
    total: 0,
    page: 1,
    totalPages: 1,
  });

  const [stats, setStats] = useState(null);

  const [params, setParams] = useState({
    page: 1,
    limit: 10,
    search: "",
    sort: "createdAt,desc",
    ...initialParams,
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);


  const fetchCourses = useCallback(
    async (overrideParams = {}) => {
      const finalParams = { ...params, ...overrideParams };

      setLoading(true);
      setError(null);

      try {
        const res = await axios.get(
          `${backendUrl}/api/instructor/courses/list`,
          {
            params: finalParams,
            withCredentials: true,
          }
        );

        const { data, pagination } = res.data.result;

        setCourses(data);
        setPagination(pagination);
        setParams(finalParams);
      } catch (err) {
        setError(err);
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [backendUrl, params]
  );

  const fetchStats = useCallback(async () => {
    try {
      const res = await axios.get(
        `${backendUrl}/api/instructor/courses/stats`,
        { withCredentials: true }
      );

      setStats(res.data.result);
    } catch (err) {
      setError(err);
      throw err;
    }
  }, [backendUrl]);

  const updateCoursePrivacy = useCallback(
    async (courseId, isPrivate) => {
      try {
        await axios.patch(
          `${backendUrl}/api/instructor/courses/${courseId}/privacy`,
          { privacy: isPrivate },
          { withCredentials: true }
        );

        // optimistic update
        setCourses((prev) =>
          prev.map((c) =>
            c.id === courseId ? { ...c, isPrivate } : c
          )
        );
      } catch (err) {
        setError(err);
        throw err;
      }
    },
    [backendUrl]
  );

  useEffect(() => {
    fetchCourses();
    fetchStats();
  }, []); // load once on page mount

  return {
    // data
    courses,
    pagination,
    stats,

    // ui state
    loading,
    error,

    // list controls
    setPage: (page) => fetchCourses({ page }),
    setSearch: (search) => fetchCourses({ page: 1, search }),
    setSort: (sort) => fetchCourses({ page: 1, sort }),
    refetchList: fetchCourses,

    // stats
    refetchStats: fetchStats,

    // actions
    updateCoursePrivacy,
  };
}
