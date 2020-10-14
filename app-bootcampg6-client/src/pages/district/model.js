import api from 'services'

const {getAllDistricts, districtSaveOrEdit, deleteDistrict, districtGetByPagable} = api
export default {
  namespace: 'district',
  state: {
    districtPage: [],
    districtTotalElements:0
  },
  subscriptions: {},
  effects: {
    * districtGetAlls({payload}, {call, put, select}) {
      const res = yield call(getAllDistricts, payload)
      yield put({
        type: 'updateState',
        payload: {
          districtPage: res.object,
        }
      })
    },
    * districtGetByPagable({payload}, {call, select, put}){
      const res =yield call(districtGetByPagable,payload)
      yield put({
        type: 'updateState',
        payload:{
          districtPage: res.object,
          districtTotalElements:res.totalElements
        }
      })
    },
    * districtSaveOrEdit({payload}, {call, select, put}) {
      const res = yield call(districtSaveOrEdit, payload)
      return res;
    },
    * deletedistrict({payload}, {call, select, put}) {
      const res = yield call(deleteDistrict, payload)
      return res;
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
