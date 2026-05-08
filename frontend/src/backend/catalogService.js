import {appFetch} from './appFetch';

export  const getBillboard = async date => await appFetch('GET', `/carteleras/cartelera?fecha=${date}`)

export const findMovieById = async id => await appFetch('GET', `/pelicula/peliculas/${id}`);