import api from 'services'

const {getCatByPageable,categorySaveOrEdit,deleteCategory} = api
export default {
  namespace: 'cat',
  state: {
    catPage: [],
    catTotalElements: 0
  },

  subscriptions: {},

  effects: {
    * getAllCategoriesByPageable({payload}, {call, select, put}) {
      const res = yield call(getCatByPageable, payload);
      yield put({
        type: 'updateState',
        payload: {
          catPage: res.object,
          catTotalElements: res.totalElements
        }
      })
    },
    * categorySaveOrEdit({payload}, {call, select, put}) {
      const res=yield call(categorySaveOrEdit,payload)
      console.log(res,"categorySaveOrEdit")
      return res;
    },
    * deleteCategory({payload},{call,select,put}){
      const res=yield call(deleteCategory,payload)
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
