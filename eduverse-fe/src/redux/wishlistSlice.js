import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import axios from "axios";

const API_URL = `${import.meta.env.VITE_BACKEND_URL}/api/wishlist`;

export const fetchWishlist = createAsyncThunk(
  "wishlist/fetchWishlist",
  async (userId, { rejectWithValue }) => {
    try {
      const response = await axios.get(API_URL, { withCredentials: true });
      return response.data.result;
    } catch (error) {
      return rejectWithValue(error.response?.data || error.message);
    }
  }
);

export const addToWishlist = createAsyncThunk(
  "wishlist/addToWishlist",
  async ({ userId, course }, { rejectWithValue }) => {
    try {
      const courseId = course.id || course._id;

      await axios.post(
        API_URL, 
        { courseId },
        { withCredentials: true } 
      );

      return {
        id: "temp-id-" + Date.now(),
        addedAt: new Date().toISOString(),
        course: course
      };
    } catch (error) {
      return rejectWithValue(error.response?.data || error.message);
    }
  }
);

export const removeFromWishlist = createAsyncThunk(
  "wishlist/removeFromWishlist",
  async ({ courseId }, { rejectWithValue }) => {
    try {
      await axios.delete(`${API_URL}/${courseId}`, {
        withCredentials: true 
      });

      return courseId;
    } catch (error) {
      return rejectWithValue(error.response?.data || error.message);
    }
  }
);

const wishlistSlice = createSlice({
  name: "wishlist",
  initialState: {
    items: [],
    status: "idle",
    error: null,
  },
  reducers: {
    clearWishlist: (state) => {
      state.items = [];
      state.status = "idle";
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchWishlist.pending, (state) => {
        state.status = "loading";
      })
      .addCase(fetchWishlist.fulfilled, (state, action) => {
        state.items = action.payload;
        state.status = "succeeded";
      })
      .addCase(fetchWishlist.rejected, (state, action) => {
        state.status = "failed";
        state.error = action.payload;
      })

      // --- Add ---
      .addCase(addToWishlist.fulfilled, (state, action) => {
        state.items.unshift(action.payload);
        state.status = "succeeded";
      })

      // --- Remove ---
      .addCase(removeFromWishlist.fulfilled, (state, action) => {
        state.items = state.items.filter(
          (item) => {
             const currentId = item.course?.id || item.course?._id;
             return currentId !== action.payload;
          }
        );
      });
  },
});

export const { clearWishlist } = wishlistSlice.actions;
export default wishlistSlice.reducer;