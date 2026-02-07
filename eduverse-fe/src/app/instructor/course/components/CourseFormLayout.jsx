import { useEffect, useRef } from "react";
import { Alert, Button, Card, CardBody, CardHeader, Col, Container, Row } from "react-bootstrap";
import { FaArrowLeft } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import useBSStepper from "@/hooks/useBSStepper";
import PageMetaData from "@/components/PageMetaData";

// Steps
import Step1 from "./step1";
import Step2 from "./step2";
import Step3 from "./step3";
import Step4 from "./step4";

const CourseFormLayout = ({
  pageTitle,
  courseDraft,
  onSaveDraft,
  onSubmit,
  isSubmitting,
  isLoading = false
}) => {
  const navigate = useNavigate();
  const stepperRef = useRef(null);
  
  // initialize stepper only when not loading and draft exists
  const stepperInstance = useBSStepper(stepperRef, !isLoading && !!courseDraft);

  // ensure stepper starts at 1
  useEffect(() => {
    if (stepperInstance) stepperInstance.to(1);
  }, [stepperInstance]);

  if (isLoading || !courseDraft) {
    return <div className="p-5 text-center">Loading course editor...</div>;
  }

  return (
    <>
      <PageMetaData title={pageTitle} />
      <Container className="mt-3 mb-5">
        <Row>
          <Col md={12} className="mx-auto text-center">
            <div className="text-start mb-3">
              <Button variant="link" onClick={() => navigate(-1)} className="p-0">
                <FaArrowLeft className="mb-1 me-2" />Return
              </Button>
            </div>
            <p className="text-center">
              {pageTitle === "Create a Course" 
                ? "Add your new course here. After submission, we will review it." 
                : "Update your course here. Changes require re-approval."}
            </p>
          </Col>
          <Col className="mx-auto text-center">
            <Alert className="mb-2 text-center" variant="primary">
              You will lose progress if you close this site or don't submit within 24h.
            </Alert>
          </Col>
        </Row>

        <Card className="bg-transparent border rounded-3 mb-5">
          <div id="stepper" ref={stepperRef} className="bs-stepper stepper-outline">
            {/* --- Stepper Header --- */}
            <CardHeader className="bg-light border-bottom px-lg-5">
              <div className="bs-stepper-header" role="tablist">
                {/* --- Stepper Headers --- */}
                <div className="step" data-target="#step-1">
                  <div className="d-grid text-center align-items-center">
                    <button type="button" className="btn btn-link step-trigger p-0 mb-2" role="tab" id="steppertrigger1" aria-controls="step-1">
                      <span className="bs-stepper-circle">1</span>
                    </button>
                    <h6 className="bs-stepper-label d-none d-md-block">Course Details <span className="text-danger">*</span></h6>
                  </div>
                </div>
                <div className="line" />
                <div className="step" data-target="#step-2">
                  <div className="d-grid text-center align-items-center">
                    <button type="button" className="btn btn-link step-trigger p-0 mb-2" role="tab" id="steppertrigger2" aria-controls="step-2">
                      <span className="bs-stepper-circle">2</span>
                    </button>
                    <h6 className="bs-stepper-label d-none d-md-block">Course Media <span className="text-danger">*</span></h6>
                  </div>
                </div>
                <div className="line" />
                <div className="step" data-target="#step-3">
                  <div className="d-grid text-center align-items-center">
                    <button type="button" className="btn btn-link step-trigger p-0 mb-2" role="tab" id="steppertrigger3" aria-controls="step-3">
                      <span className="bs-stepper-circle">3</span>
                    </button>
                    <h6 className="bs-stepper-label d-none d-md-block">Curriculum <span className="text-danger">*</span></h6>
                  </div>
                </div>
                <div className="line" />
                <div className="step" data-target="#step-4">
                  <div className="d-grid text-center align-items-center">
                    <button type="button" className="btn btn-link step-trigger p-0 mb-2" role="tab" id="steppertrigger4" aria-controls="step-4">
                      <span className="bs-stepper-circle">4</span>
                    </button>
                    <h6 className="bs-stepper-label d-none d-md-block">Additional Information</h6>
                  </div>
                </div>
              </div>
            </CardHeader>

            {/* --- Stepper Content --- */}
            <CardBody>
              <div className="bs-stepper-content">
                <Step1 stepperInstance={stepperInstance} draftData={courseDraft} onSave={onSaveDraft} />
                <Step2 stepperInstance={stepperInstance} draftData={courseDraft} onSave={onSaveDraft} />
                <Step3 stepperInstance={stepperInstance} draftData={courseDraft} onSave={onSaveDraft} />
                <Step4 
                  stepperInstance={stepperInstance} 
                  draftData={courseDraft} 
                  onSave={onSaveDraft} 
                  onSubmit={onSubmit} 
                  isSubmitting={isSubmitting} 
                />
              </div>
            </CardBody>
          </div>
        </Card>
      </Container>
    </>
  );
};

export default CourseFormLayout;