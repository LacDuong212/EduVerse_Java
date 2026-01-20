import { useEffect, useState } from 'react';
import { Col, Container, Row } from 'react-bootstrap';
import CountUp from 'react-countup';
import {
  FaBookOpen,
  FaUserGraduate,
  FaClock,
  FaChalkboardTeacher
} from 'react-icons/fa';
import axios from "axios";


const Counter = () => {

  const backendUrl = import.meta.env.VITE_BACKEND_URL;

  const [stats, setStats] = useState({
    totalCourses: 0,
    totalLearners: 0,
    totalHours: 0,
    totalInstructors: 0
  });

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const response = await axios.get(`${backendUrl}/api/courses/stats`); 
        if (response.data && response.data.result) {
          setStats(response.data.result);
        }
      } catch (error) {
        console.error("Failed to fetch course stats:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  const counterData = [
    { icon: FaBookOpen,          variant: 'primary', count: stats.totalCourses,     suffix: '',  title: 'Courses', decimals: 0 },
    { icon: FaUserGraduate,      variant: 'success', count: stats.totalLearners,    suffix: '',  title: 'Learners', decimals: 0 },
    { icon: FaClock,             variant: 'warning', count: stats.totalHours,       suffix: 'h', title: 'Total Hours', decimals: 2 },
    { icon: FaChalkboardTeacher, variant: 'info',    count: stats.totalInstructors, suffix: '',  title: 'Instructors', decimals: 0 },
  ];

  if (loading) return null;

  return (
    <section className="pt-0 pt-xl-5 pb-5">
      <Container>
        <Row className="g-4">
          {counterData.map((item, idx) => {
            const Icon = item.icon;
            return (
              <Col sm={6} xl={3} key={idx}>
                <div className={`d-flex justify-content-center align-items-center p-4 bg-${item.variant} bg-opacity-10 rounded-3`}>
                  <span className={`display-6 lh-1 text-${item.variant} mb-0`} aria-hidden>
                    <Icon />
                  </span>
                  <div className="ms-4 h6 fw-normal mb-0">
                    <div className="d-flex">
                      <h5 className="purecounter mb-0 fw-bold">
                        <CountUp
                          delay={0.2}
                          end={Number.isFinite(item.count) ? item.count : 0}
                          suffix={item.suffix}
                          duration={1.5}
                          preserveValue={false}
                          decimals={item.decimals}
                          decimal=","
                          separator="."
                        />
                      </h5>
                    </div>
                    <p className="mb-0">{item.title}</p>
                  </div>
                </div>
              </Col>
            );
          })}
        </Row>
      </Container>
    </section>
  );
};

export default Counter;
