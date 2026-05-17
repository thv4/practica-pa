import {combineReducers} from 'redux';
import * as actionTypes from './actionTypes';

const initialState = {
    movies: null,
    billboardDate: null,
    session: null,
    lastPurchased: null,
};

const movies = (state = initialState.movies, action) => {
    switch (action.type) {
        case actionTypes.GET_BILLBOARD_COMPLETED:
            return action.movies;
        case actionTypes.CLEAR_BILLBOARD:
            return null;
        default:
            return state;
    }
}
const billboardDate = (state = initialState.billboardDate, action) => {
    switch (action.type) {
        case actionTypes.CLEAR_BILLBOARD:
            return action.date;
        default:
            return state;
    }
}

const session = (state = initialState.session, action) => {
    switch (action.type) {
        case actionTypes.GET_SESSION_COMPLETED:
            return action.session;
        case actionTypes.CLEAR_SESSION:
            return null;
        default:
            return state;
    }
};

const lastPurchasedId = (state = null, action) => {
    switch(action.type){
        case actionTypes.BUY_TICKETS_COMPLETED:
            return action.compraId;
        default:
            return state;
    }
};

const lastDelivery = (state = null, action) => {
    switch (action.type) {
        case actionTypes.DELIVER_TICKETS_COMPLETED:
            return action.entregaResult;
        default:
            return state;
    }
}

const reducer = combineReducers({
    movies,
    billboardDate,
    session,
    lastPurchasedId,
    lastDelivery,
});

export default reducer;