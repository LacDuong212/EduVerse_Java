import authReducer from './authSlice';
import coursesReducer from './coursesSlice';
import cartReducer from './cartSlice';
import wishlistReducer from './wishlistSlice';
import { combineReducers } from "redux";
import { configureStore } from '@reduxjs/toolkit';

// combine all reducers
const rootReducer = combineReducers({
  auth: authReducer,
  courses: coursesReducer,
  cart: cartReducer,
  wishlist: wishlistReducer
});

// configure store
export const store = configureStore({
  reducer: rootReducer,
});
