import {appFetch} from './appFetch';

export  const getBillboard = async date => await appFetch('GET', `/carteleras/cartelera?fecha=${date}`)

export const findMovieById = async id => await appFetch('GET', `/pelicula/peliculas/${id}`);

export const  getSession = async sessionId => await appFetch('GET', `/sesion/${sessionId}`);

export const buyTickets = async (sesionId, numLocalidades, tarjetaBancaria) => await appFetch('POST', '/compras/buy', {sesionId, numLocalidades, tarjetaBancaria});

export const getPurchaseHistory = async (page) => await appFetch('GET', `/compras/compras?page=${page}`);