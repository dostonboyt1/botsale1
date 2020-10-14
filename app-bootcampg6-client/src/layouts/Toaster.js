import {toast} from "react-toastify";

export const Toster = (succ,msg) =>{
  if (succ) {
    toast(msg);
  } else {
    toast.error(msg);
  }
};
