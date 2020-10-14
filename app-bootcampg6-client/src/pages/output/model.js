import api from 'services'

const {getAllInputsByPageable,getAllProductsByCatAndBrand,getProductSizeListByProduct,saveInputOrOutput,removeInputOrOutput} = api

export default {
  namespace:"output",
  state:{
    allOutputByPageable:[],
    allOutputTotalElement:0
  },
  subscriptions:{},
  effects:{
    * getAllOutputByPageable({payload},{call,select,put}){
      const res=yield call(getAllInputsByPageable,payload)
      console.log(res,"AllInputsByPageable")
      yield put({
        type:'updateState',
        payload:{
          allOutputByPageable:res.object,
          allOutputTotalElement:res.totalElements
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
    * saveOrEditOutput({payload},{call,select,put}){
      const res=yield call(saveInputOrOutput,payload)
      return res
    },
    *removeInputOrOutput({payload},{call,select,put}){
      const res=yield call(removeInputOrOutput,payload)
      return res
    },
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
