import api from 'services'

const {getProductByPageable,productSaveOrEdit,deleteProduct,changeActive} = api
export default {
  namespace: 'product',
  state: {
    productPage: [],
    productTotalElements: 0
  },

  subscriptions: {},

  effects: {
    * getAllProductsByPageable({payload}, {call, select, put}) {
      const res = yield call(getProductByPageable, payload);
      console.log(res,"PRODUCT SEARCH")
      yield put({
        type: 'updateState',
        payload: {
          productPage: res.object,
          productTotalElements: res.totalElements
        }
      })
    },
    * productSaveOrEdit({payload}, {call, select, put}) {
      const res=yield call(productSaveOrEdit,payload)
      return res;
    },
    * deleteProduct({payload},{call,select,put}){
      const res=yield call(deleteProduct,payload)
      return res;
    },
    * changeActive({payload},{call,select,put}){
      const res=yield call(changeActive,payload)
      return res;
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
