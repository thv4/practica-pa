import {MovieLink} from '../../common';

const Movies = ({ movies }) => {

    if (!movies || movies.length === 0) {
        return (
            <div className="alert alert-info" role="alert">
                No hay películas disponibles para el día seleccionado.
            </div>
        );
    }

    return (
        <div className="container mt-4">
            <div className="row">
                {movies.map(({pelicula, sesiones}) => (
                    <div className="col-12 mb-4" key={pelicula.id}>
                        <div className="card shadow-sm">
                            <div className="card-body">
                                <h5 className="card-title text-primary"><MovieLink id={pelicula.id} name={pelicula.titulo}/></h5>
                                <div className="d-flex flex-wrap mt-2">
                                    {/* Desestructuramos también aquí para que el IDE vea 'hora' */}
                                    {sesiones.map(({id, hora}) => (
                                        <span key={id} className="badge bg-dark m-1 p-2">
                                            {hora}
                                        </span>
                                    ))}
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Movies;