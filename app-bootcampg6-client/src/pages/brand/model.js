import api from 'services'

const {getBrandByPageable, brandSaveOrEdit, changeActiveBrand, brandDelete} = api
export default {
  namespace: 'brand1',
  state: {
    brandPage: [],
    brandTotalElements: 0
  },

  subscriptions: {},

  effects: {
    * getBrandByPageable({payload}, {call, put, select}) {
      const res = yield call(getBrandByPageable, payload);
      yield put({
        type: 'updateState',
        payload: {
          brandPage: res.object.content,
          brandTotalElements: res.totalElements
        }
      })
    },
    * brandSaveOrEdit({payload}, {call, select, put}) {
      const res = yield call(brandSaveOrEdit, payload)
      return res;
    },
    * brandDelete({payload}, {call, select, put}) {
      const res = yield call(brandDelete, payload)
      return res;
    },
    * changeActiveBrand({payload}, {call, select, put}) {
      const res = yield  call(changeActiveBrand, payload)
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
