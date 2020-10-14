import api from 'services'

const {getAllInputsByPageable,getAllProductsByCatAndBrand,getProductSizeListByProduct,saveInputOrOutput,
  removeInputOrOutput,confirmInput} = api

export default {
  namespace:"input",
  state:{
    allInputsByPageable:[],
    allInputsTotalElement:0,
    allProductsByCatAndBrand:[],
    sizeListByProduct:[],
    selectedProduct:''
  },
  subscriptions:{},
  effects:{
    * getAllInputsByPageable({payload},{call,select,put}){
      const res=yield call(getAllInputsByPageable,payload)
      console.log(res,"AllInputsByPageable")
      yield put({
        type:'updateState',
        payload:{
          allInputsByPageable:res.object,
          allInputsTotalElement:res.totalElements
        }
      })
    },
    * getAllProductsByCatAndBrand({payload},{call,select,put}){
      const res=yield call(getAllProductsByCatAndBrand,payload)
      console.log(res,"PRODUCTBYCATORBRAND")
      yield put({
        type:'updateState',
        payload:{
          allProductsByCatAndBrand:res.object
        }
      })
      return res
    },
    * getProductSizeListByProduct({payload},{call,select,put}){
      const res=yield call(getProductSizeListByProduct,payload)
      console.log(res,"SIZELIST")
      if (res.success){
        yield put({
          type:'updateState',
          payload:{
            sizeListByProduct:res.object.productSizeList,
            selectedProduct:res.object
          }
        })
      }
      return res
    },
    * saveIncomeOrOutput({payload},{call,select,put}){
      const res=yield call(saveInputOrOutput,payload)
      console.log(res,"saveIncomeOrOutput")
      return res
    },
    *removeInputOrOutput({payload},{call,select,put}){
      const res=yield call(removeInputOrOutput,payload)
      return res
    },
    * confirmInput({payload},{call,select,put}){
      const res=yield call(confirmInput,payload)
      return res
    }
  },
  reducers: {
    updateState(state, {payload}) {
      return {
        ...state,
        ...payload,
      }
    }


  }
}
