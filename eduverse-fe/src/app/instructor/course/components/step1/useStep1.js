import { useState, useEffect } from "react";
import { toast } from "react-toastify";
import { useCategories } from "@/hooks/useCategories";

export const useStep1 = (draftData, onSave, stepperInstance) => {
  const [saving, setSaving] = useState(false);
  const [errors, setErrors] = useState({});
  
  const { categories, loading: categoriesLoading } = useCategories();

  const [formData, setFormData] = useState({
    title: '', subtitle: '', categoryId: '',
    level: '', language: '', duration: '', durationUnit: "second",
    price: '', discountPrice: '', enableDiscount: false,
    description: '', isPrivate: false,
  });

  // utils
  const formatCurrency = (val) => {
    if (val === 0) return '0';
    if (!val) return '';
    return new Intl.NumberFormat("en-US").format(Number(String(val).replace(/[^0-9.-]/g, '')));
  };

  const parseCurrency = (str) => {
    const digits = String(str || '').replace(/[^0-9.-]/g, '');
    return digits === '' ? NaN : Number(digits);
  };

  // lifecycle
  useEffect(() => {
    if (draftData) {
      setFormData(prev => ({
        ...prev,
        ...draftData,
        price: formatCurrency(draftData.price),
        discountPrice: formatCurrency(draftData.discountPrice),
      }));
    }
  }, [draftData]);

  // handlers
  const updateField = (name, value) => {
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors(prev => ({ ...prev, [name]: null }));
  };

  const handleChange = (e) => {
    const { name, type, checked, value } = e.target;
    let finalValue = type === "checkbox" ? checked : value;
    if (name === "price" || name === "discountPrice") finalValue = formatCurrency(value);
    updateField(name, finalValue);
  };

  const validate = () => {
    const errs = {};
    const p = parseCurrency(formData.price);
    const d = parseCurrency(formData.discountPrice);

    if (!formData.title?.trim()) errs.title = "Title is required";
    if (!formData.category) errs.category = "Category is required";
    if (!formData.level) errs.level = "Level is required";
    if (!formData.language) errs.language = "Language is required";
    if (isNaN(p) || p < 0) errs.price = "Valid price is required";

    if (formData.enableDiscount) {
      if (isNaN(d) || d <= 0) errs.discountPrice = "Valid discount required";
      else if (d >= p) errs.discountPrice = "Must be less than price";
    }

    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return toast.error("Please recheck the form for errors");

    setSaving(true);
    const payload = {
      ...formData,
      price: parseCurrency(formData.price),
      discountPrice: parseCurrency(formData.discountPrice) || 0
    };
    onSave(payload);
    stepperInstance?.next();
    toast.success("Step 1 saved");
    setSaving(false);
  };

  return { 
    formData, 
    errors, 
    categories, 
    categoriesLoading,
    saving, 
    handleChange, 
    updateField, 
    handleSubmit 
  };
};