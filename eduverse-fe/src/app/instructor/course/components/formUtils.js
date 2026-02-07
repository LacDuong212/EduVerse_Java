
export const hasMeaningfulDraftData = (draft) => {
  if (!draft) return false;
  const keys = Object.keys(draft);
  if (keys.length === 0) return false;

  // check if there is any key other than "tags"
  const hasOtherData = keys.some(key => key !== "tags");
  if (hasOtherData) return true;

  // only has "tags" key, check if tags is empty
  if (draft.tags && draft.tags.length > 0) return true;

  return false;
};

export const validateDraft = (draft) => {
  // Step 1
  if (!draft.title?.trim()) return "Title is required.";
  if (!draft.category?.trim()) return "Category is required.";
  if (!draft.level?.trim()) return "Level is required.";
  if (!draft.language?.trim()) return "Language is required.";
  if (draft.price == null || draft.price < 0) return "A valid price is required.";
  
  if (draft.enableDiscount) {
    if (draft.discountPrice == null || draft.discountPrice < 0) return "Discount price is required.";
    if (draft.discountPrice >= draft.price) return "Discount price must be less than original price.";
  }

  // Step 2
  if (!draft.image?.trim()) return "Course image is required.";

  // Step 3
  if (!draft.curriculum || draft.curriculum.length === 0) {
    return "Curriculum must have at least one section.";
  } else if (draft.curriculum.some(sec => sec.lectures.length === 0)) {
    return "Each section must contain at least one lecture.";
  }

  return null; // Valid
};

export const handleAxiosError = (error, defaultMsg) => {
  console.error(defaultMsg, error);
  if (error.response) {
    const { status, data } = error.response;
    const msg = data?.message;
    switch (status) {
      case 413: return "Course too large! Please reduce size.";
      case 400: return msg || "Invalid data. Check inputs.";
      case 401: return msg || "Session expired. Login again.";
      case 403: return msg || "Permission denied.";
      case 404: return msg || "Course not found.";
      case 500: return msg || "Server error.";
      default: return msg || `Error: ${status}`;
    }
  }
  return error.request ? "Network error." : error.message || defaultMsg;
};