import api from 'services'
import {STORAGE_NAME} from '../utills/constant';
import {Toster} from "../layouts/Toaster";

const {login,userMe,getAllCategories,getAllPayTypes,getAllRegion,addOrEditFile,getAllBrands,getAllProductSize,getAllWarehouseWithoutOutputer}=api

export default ({
  name: "app",
  state: {
    currentUser:'',
    allCategories:[],
    file:'',
    allBrands:[],
    allProductSize:[],
    allRegions:[],
    allWarehouseListWithoutOutputer:[],
    allPayTypes:[]
  },
  subscriptions: {},
  effects: {
    * signIn({payload}, {call, put, select}) {
      const res=yield call(login,payload)
      if (res.success){
        localStorage.setItem(STORAGE_NAME,res.body.tokenType+" "+res.body.accessToken)
        return res;
      }else {
        Toster(false,"Login yoki parol noto'g'ri");
      }

    },
    * userme({payload},{call,select,put}){
      const res=yield call(userMe)
      console.log(res,"USERME")
      if (res.success){
        yield put({
          type:'updateState',
          payload:{
            currentUser:res.object
          }
        })
      }
      return res;
    },
    * getAllCategories({payload},{call,select,put}){
      const res=yield call(getAllCategories)
      console.log(res,"RES===")
      if (res.success){
        yield put({
          type:'updateState',
          payload:{
            allCategories:res.object
          }
        })
      }

    },
    * getAllBrands({payload},{call,select,put}){
      const res=yield call(getAllBrands)
      console.log(res,"RES===Brand")
      if (res.success){
        yield put({
          type:'updateState',
          payload:{
            allBrands:res.object
          }
        })
      }

    },
    * getAllProductSize({payload},{call,select,put}){
      const res=yield call(getAllProductSize)
      console.log(res,"RES===PRoductSize")
      if (res.success){
        yield put({
          type:'updateState',
          payload:{
            allProductSize:res.object
          }
        })
      }

    },
    * addOrEditFile({payload}, {call, put, select}) {
      const res = yield call(addOrEditFile, payload);
      if (res.success) {
        yield put({
          type: 'updateState',
          payload: {
            file: res.object
          }
        })
      }
      return res
    },
    * getAllRegion({payload},{call,put,select}){
      const res= yield call(getAllRegion);
      if (res.success){
        yield put({
          type:'updateState',
          payload: {
            allRegions:res.object
          }
        })
      }
    },
    * getAllWarehouseWithoutOutputer({payload},{call,select,put}){
      const res=yield call(getAllWarehouseWithoutOutputer)
      yield put({
        type:'updateState',
        payload:{
          allWarehouseListWithoutOutputer:res.object
        }
      })
    },
    * getAllPayTypes({payload},{call,select,put}){
      const res=yield call(getAllPayTypes,payload)
      console.log(res,"getAllPayTypes<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>")
      yield put({
        type:'updateState',
        payload:{
          allPayTypes:res.object
        }
      })
    }

  },
  reducers: {
    updateState(state, {payload}) {
      return {
        ...state,
        ...payload
      }
    }
  }
})
