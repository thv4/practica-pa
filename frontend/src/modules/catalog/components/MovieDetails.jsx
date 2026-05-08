import {useState, useEffect} from 'react';
import {useParams} from 'react-router';

import backend from '../../../backend';
import {BackLink} from '../../common';
import Container from "react-bootstrap/Container";
import {Badge, ListGroup, Card, Row, Col} from "react-bootstrap";

const MovieDetails = () => {

    const [movie, setProduct] = useState(null);
    const {id} = useParams();
    const movieId = Number(id);

    useEffect(() => {

        const findMovietById = async movieId => {
            if (!Number.isNaN(movieId)) {
                const response = await backend.catalogService.findMovieById(movieId);
                if (response.ok) {
                    setProduct(response.payload);
                }
            }
        }

        findMovietById(movieId);

    }, [movieId]);

    if (!movie) {
        return null;
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
                                    🎬 Película
                                </Badge>
                                <span className="text-muted fw-bold">
                                    ⏱️ {movie.duracion} min
                                </span>
                            </div>

                            <Card.Subtitle className="mb-2 text-muted text-uppercase small tracking-widest">
                                Sinopsis
                            </Card.Subtitle>

                            <Card.Text className="lead text-secondary" style={{ textAlign: 'justify' }}>
                                {movie.resumen}
                            </Card.Text>

                            <hr />

                            <ListGroup variant="flush" className="small">
                                <ListGroup.Item className="d-flex justify-content-between border-0 px-0">
                                    <strong>Identificador:</strong>
                                    <span>#{movie.id}</span>
                                </ListGroup.Item>
                            </ListGroup>
                        </Card.Body>

                        <Card.Footer className="bg-light text-center py-2">
                            <small className="text-muted">Información proporcionada por la Cartelera Oficial</small>
                        </Card.Footer>
                    </Card>
                </Col>
            </Row>
        </Container>
    );

}

export default MovieDetails;
