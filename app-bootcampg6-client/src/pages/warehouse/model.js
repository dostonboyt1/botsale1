import api from 'services'

const {getAllWarehouse, getAllRegions, getAllDistrict, getAllUsers, warehouseSaveOrEdit,deleteWarehouse} = api

export default {
  namespace: "warehouse",
  state: {
    allWarehouse: [],
    allRegion: [],
    allDistrict: [],
    allUser: []
  },
  subscriptions: {},
  effects: {
    * getAllWarehouse({payload}, {call, select, put}) {
      const res = yield call(getAllWarehouse);
      console.log(res, "Warehouse===RES")
      yield put({
        type: 'updateState',
        payload: {
          allWarehouse: res.list,
        }
      })
    },
    * getAllRegion({payload}, {call, select, put}) {
      const res = yield call(getAllRegions);
      console.log(res, "Region===RES")
      yield put({
        type: 'updateState',
        payload: {
          allRegion: res.list,
        }
      })
    },
    * getAllDistrict({payload}, {call, select, put}) {
      yield put({
        type: 'updateState',
        payload: {
          allDistrict: [],
        }
      })
      const res = yield call(getAllDistrict, payload);
      console.log(res, "District===RES")
      yield put({
        type: 'updateState',
        payload: {
          allDistrict: res.list,
        }
      })
      return res
    },
    * getAllUsers({payload}, {call, select, put}) {
      const res = yield call(getAllUsers);
      console.log(res, "Users===RES")
      yield put({
        type: 'updateState',
        payload: {
          allUser: res.list,
        }
      })
    },
    * saveOrEdit({payload}, {call, select, put}) {
      const res = yield call(warehouseSaveOrEdit, payload);
      return res
    },
    * deleteWarehouse({payload}, {call, select, put}) {
      const res = yield call(deleteWarehouse, payload);
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
