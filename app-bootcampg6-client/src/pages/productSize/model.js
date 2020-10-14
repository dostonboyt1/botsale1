import api from 'services'

const {getProductSizeByPageable, saveOrEditProductSize, deleteProductSize} = api
export default {
  namespace: 'productSize',
  state: {
    productSizePage: [],
    productSizeTotalElements: 0
  },

  subscriptions: {},

  effects: {
    * getProductSizeByPageable({payload}, {call, put, select}) {
      const res = yield call(getProductSizeByPageable, payload);
      yield put({
        type: 'updateState',
        payload: {
          productSizePage: res.object.content,
          productSizeTotalElements: res.totalElements
        }
      })
      return res;
    },
    * saveOrEditProductSize({payload}, {call, put, select}) {
      const res = yield call(saveOrEditProductSize, payload);
      return res
    },
    * deleteProductSize({payload}, {call, put, select}) {
      const res = yield call(deleteProductSize, payload);
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
