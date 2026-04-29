import {combineReducers} from 'redux';
import * as actionTypes from './actionTypes';

const initialState = {
    movies: null,
};

const movies = (state = initialState.movies, action) => {
    switch (action.type) {
        case actionTypes.GET_BILLBOARD_COMPLETED:
            return action.movies;
        default:
            return state;
    }
}

const reducer = combineReducers({
    movies
});

export default reducer;