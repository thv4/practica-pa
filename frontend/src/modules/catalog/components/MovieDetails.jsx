import {useState, useEffect} from 'react';
import {useParams} from 'react-router';

import backend from '../../../backend';
import {BackLink, Errors} from '../../common';
import Container from "react-bootstrap/Container";
import {Badge, Card, Row, Col} from "react-bootstrap";
import {FormattedMessage} from "react-intl";

const MovieDetails = () => {

    const [movie, setProduct] = useState(null);
    const [backendErrors, setBackendErrors] = useState(null);
    const {id} = useParams();
    const movieId = Number(id);
    const [loading, setLoading] = useState(true);

    useEffect(() => {

        const findMovietById = async movieId => {
            setLoading(true);
            setBackendErrors(null);
            if (!Number.isNaN(movieId)) {
                const response = await backend.catalogService.findMovieById(movieId);
                if (response.ok) {
                    setProduct(response.payload);
                } else {
                    setBackendErrors(response.payload);
                    setProduct(null);
                }
            } else {
                setBackendErrors({ globalError: "ID de película inválido" });
            }
            setLoading(false);
        }

        findMovietById(movieId);

    }, [movieId]);

    if (loading) {
        return null;
    }

    if (backendErrors) {
        return (
            <div className="container mt-4">
                <div className="mb-3">
                    <BackLink/>
                </div>
                <Errors errors={backendErrors} onClose={() => setBackendErrors(null)} />
            </div>
        );
    }

    if (!movie) {
        return (
            <div className="container mt-4">
                <div className="mb-3">
                    <BackLink/>
                </div>
                <div className="alert alert-danger" role="alert">
                    <FormattedMessage id="project.catalog.MovieDetails.notFound" />
                </div>
            </div>
        );
    }

    return (
        <Container className="mt-4">
            <div className="mb-3">
                <BackLink />
            </div>

            <Row className="justify-content-center">
                <Col md={8} lg={6}>
                    <Card className="shadow-lg border-0 overflow-hidden">
                        <Card.Header className="bg-primary text-white text-center py-3">
                            <h2 className="mb-0 fw-bold">{movie.titulo}</h2>
                        </Card.Header>

                        <Card.Body className="p-4">
                            <div className="d-flex justify-content-between align-items-center mb-3">
                                <Badge bg="dark" className="p-2">
                                    🎬 <FormattedMessage id="project.catalog.MovieDetails.movie" />
                                </Badge>
                                <span className="text-muted fw-bold">
                                    ⏱️ <FormattedMessage id="project.catalog.MovieDetails.duration" />: {movie.duracion} min
                                </span>
                            </div>

                            <Card.Subtitle className="mb-2 text-muted text-uppercase small tracking-widest">
                                <FormattedMessage id="project.catalog.MovieDetails.synopsis" />
                            </Card.Subtitle>

                            <Card.Text className="lead text-secondary" style={{ textAlign: 'justify' }}>
                                {movie.resumen}
                            </Card.Text>
                        </Card.Body>

                        <Card.Footer className="bg-light text-center py-2">
                            <small className="text-muted"><FormattedMessage id="project.catalog.MovieDetails.info" /></small>
                        </Card.Footer>
                    </Card>
                </Col>
            </Row>
        </Container>
    );

}

export default MovieDetails;
