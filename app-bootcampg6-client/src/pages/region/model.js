import api from 'services'

const {getAllRegion,regionSaveOrEdit,deleteRegion} = api
export default {
  namespace: 'region',
  state: {
    regionPage: [],
  },
  subscriptions: {},
  effects: {
    * getAllRegion({payload}, {call, put, select}) {
      const res = yield call(getAllRegion, payload)
      yield put({
        type: 'updateState',
        payload: {
          regionPage: res.object,
        }
      })
    },
        * regionSaveOrEdit({payload},{call, select, put}) {
          const res=yield call(regionSaveOrEdit,payload)
          return res;
        },
        * deleteRegion({payload},{call, select, put}){
          const res=yield call(deleteRegion,payload)
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
