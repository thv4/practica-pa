import {useState, useEffect} from 'react';
import {useParams} from 'react-router';
import {BackLink} from "../../common";
import {useSelector, useDispatch} from 'react-redux';
import Card from 'react-bootstrap/Card';
import Button from 'react-bootstrap/Button';
import backend from '../../../backend';
import * as selectors from '../selectors';
import * as actions from '../actions';
import {FormattedMessage} from 'react-intl';

const SessionDetails = () => {

    const {id} = useParams();
    const sessionId = Number(id);
    const [session, setProduct] = useState(null);

    useEffect(() => {

        const loadSession = async sessionId => {
            if (!Number.isNaN(sessionId)) {
                const response = await backend.catalogService.getSession(sessionId);
                if (response.ok) {
                    //dispatch(actions.getSessionCompleted(response.payload));
                    setProduct(response.payload);
                }
            }
        };

        loadSession(sessionId);

    }, [sessionId]);

    if (!session) {
        return (
            <div className="alert alert-danger" role="alert">
                <FormattedMessage id="project.catalog.SessionDetails.notFound" />
            </div>
        );
    }

    return (
        <div className="container mt-4">
            <div className="mb-3">
                <BackLink/>
            </div>
            <Card>
                <Card.Body>
                    <Card.Title>{session.tituloPelicula}</Card.Title>
                    <Card.Subtitle className="mb-2 text-muted">
                        {session.duracionPelicula} min
                    </Card.Subtitle>
                    <Card.Text>
                        <strong><FormattedMessage id="project.catalog.SessionDetails.sala" />:</strong> {session.nombreSala}<br />
                        <strong><FormattedMessage id="project.catalog.SessionDetails.date" />:</strong> {session.fechaHora}<br />
                        <strong><FormattedMessage id="project.catalog.SessionDetails.price" />:</strong> {session.precio} €<br />
                        <strong><FormattedMessage id="project.catalog.SessionDetails.availableSeats" />:</strong> {session.localidadesDisponibles}
                    </Card.Text>

                    {/* Aquí se añadirá el formulario de compra en la siguiente iteración */}

                </Card.Body>
            </Card>
        </div>
    );
};

export default SessionDetails;