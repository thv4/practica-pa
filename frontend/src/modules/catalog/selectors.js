const getModuleState = state => state.catalog;

export const getMovies = state => {
    const moduleState = getModuleState(state);
    return moduleState ? moduleState.movies : [];
};

export const getBillboardDate = state => {
    const moduleState = getModuleState(state);
    return moduleState ? moduleState.billboardDate : null;
};