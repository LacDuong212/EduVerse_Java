import axios from "axios";
import { useCallback, useEffect, useState } from "react";

export default function useInstructorMyStudents(initialParams = {}) {
  const backendUrl = import.meta.env.VITE_BACKEND_URL;

  const [students, setStudents] = useState([]);
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
    sort: "nameAsc",
    ...initialParams,
  });

  const [statsLoading, setStatsLoading] = useState(false);
  const [studentsLoading, setStudentsLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchStudents = useCallback(
    async (overrideParams = {}) => {
      const finalParams = { ...params, ...overrideParams };

      setStudentsLoading(true);
      setError(null);

      try {
        const res = await axios.get(
          `${backendUrl}/api/instructor/students/list`,
          {
            params: finalParams,
            withCredentials: true,
          }
        );

        const { data, pagination } = res.data.result;

        setStudents(data);
        setPagination(pagination);
        setParams(finalParams);
      } catch (err) {
        setError(err);
        throw err;
      } finally {
        setStudentsLoading(false);
      }
    },
    [backendUrl, params]
  );

  const fetchStats = useCallback(async () => {
    setStatsLoading(true);
    setError(null);

    try {
      const { data } = await axios.get(
        `${backendUrl}/api/instructor/students/stats`,
        { withCredentials: true }
      );

      if (data.success)
        setStats(data.result);
      else setError(data.message);
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      setStatsLoading(false);
    }
  }, [backendUrl]);

  useEffect(() => {
    fetchStudents();
    fetchStats();
  }, []); // load once on page mount

  return {
    // data
    students,
    pagination,
    stats,

    // ui state
    statsLoading,
    studentsLoading,
    error,

    // list controls
    setPage: (page) => fetchStudents({ page }),
    setSearch: (search) => fetchStudents({ page: 1, search }),
    setSort: (sort) => fetchStudents({ page: 1, sort }),
    refetchList: fetchStudents,

    // stats
    refetchStats: fetchStats,
  };
}
