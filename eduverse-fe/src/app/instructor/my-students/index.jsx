import { Container } from "react-bootstrap";
import PageMetaData from "@/components/PageMetaData";
import Preloader from "@/components/Preloader";
import useInstructorMyStudents from "./useInstructorMyStudents";
import MyStudentsHero from "./components/Hero";
import MyStudentsList from "./components/MyStudents";

const InstructorMyStudents = () => {
  const {
    students,
    pagination,
    stats,
    statsLoading,
    studentsLoading,

    setPage,
    setSearch,
    setSort,
  } = useInstructorMyStudents();

  if (statsLoading) {
    return (
      <Preloader />
    );
  }

  return (
    <>
      <PageMetaData title="My Students" />
      <MyStudentsHero stats={stats} />
      <Container className="my-5">
        <MyStudentsList 
          students={students}
          totalStudents={pagination.total}
          page={pagination.page}
          limit={pagination.limit || 5}
          totalPages={pagination.totalPages}
          loading={studentsLoading}
          onPageChange={setPage}
          onSearch={setSearch}
          onSortChange={setSort}
        />
      </Container>
    </>
  );
};

export default InstructorMyStudents;
