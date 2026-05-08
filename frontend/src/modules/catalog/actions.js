import * as actionTypes from './actionTypes';

export const getBillboardCompleted = movies => ({
    type: actionTypes.GET_BILLBOARD_COMPLETED,
    movies
});

export const clearBillboard = date => ({
    type: actionTypes.CLEAR_BILLBOARD,
    date
});

export const getSessionCompleted = session => ({
    type: actionTypes.GET_SESSION_COMPLETED,
    session
});

export const clearSession = () => ({
    type: actionTypes.CLEAR_SESSION
});