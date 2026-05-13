import {useState, useEffect} from 'react';
import {useParams} from 'react-router';
import {BackLink, Errors} from "../../common";
import {useSelector, useDispatch} from 'react-redux';
import Card from 'react-bootstrap/Card';
import Button from 'react-bootstrap/Button';
import backend from '../../../backend';
import * as selectors from '../selectors';
import * as actions from '../actions';
import {FormattedMessage} from 'react-intl';

import {useNavigate} from 'react-router';
import users from "../../users";

const SessionDetails = () => {

    const {id} = useParams();
    const sessionId = Number(id);
    const [session, setSession] = useState(null);
    const [backendErrors, setBackendErrors] = useState(null);
    const [loading, setLoading] = useState(true);

    const navigate = useNavigate();
    const isLoggedIn = useSelector(users.selectors.isLoggedIn);

    useEffect(() => {

        const loadSession = async () => {
            setLoading(true);
            setBackendErrors(null);
            if (!Number.isNaN(sessionId)) {
                const response = await backend.catalogService.getSession(sessionId);
                if (response.ok) {
                    setSession(response.payload);
                } else {
                    setBackendErrors(response.payload);   //Guarda el error del backend
                    setSession(null);
                }
            } else {
                setBackendErrors({ globalError: "ID de sesión inválido" });
            }
            setLoading(false);
        };

        loadSession();

    }, [sessionId]);

    // Mientras carga, no mostrar nada (o un spinner)
    if (loading) {
        return null;
    }

    // Si hay errores devueltos por el backend, mostrarlos con el componente Errors
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

    // Si no hay sesión (y no hay errores), mostrar mensaje "no encontrada"
    if (!session) {
        return (
            <div className="container mt-4">
                <div className="mb-3">
                    <BackLink/>
                </div>
                <div className="alert alert-danger" role="alert">
                    <FormattedMessage id="project.catalog.SessionDetails.notFound" />
                </div>
            </div>
        );
    }

    // Sesión cargada correctamente: mostrar detalles
    return (
        <div className="container mt-4">
            <div className="mb-3">
                <BackLink/>
            </div>
            <Card>
                <Card.Body>
                    <Card.Title>{session.tituloPelicula}</Card.Title>
                    <Card.Subtitle className="mb-2 text-muted">
                        <FormattedMessage id="project.catalog.SessionDetails.duration" />: {session.duracionPelicula} min
                    </Card.Subtitle>
                    <Card.Text>
                        <strong><FormattedMessage id="project.catalog.SessionDetails.sala" />:</strong> {session.nombreSala}<br />
                        <strong><FormattedMessage id="project.catalog.SessionDetails.date" />:</strong> {session.fechaHora}<br />
                        <strong><FormattedMessage id="project.catalog.SessionDetails.price" />:</strong> {session.precio} €<br />
                        <strong><FormattedMessage id="project.catalog.SessionDetails.availableSeats" />:</strong> {session.localidadesDisponibles}
                    </Card.Text>

                    {isLoggedIn && (
                        <Button
                            variant="primary"
                            onClick={() => navigate(`/catalog/buy/${sessionId}`)}>
                            <FormattedMessage id="project.catalog.BuyTickets.buttons.buy"/>
                        </Button>
                    )}

                </Card.Body>
            </Card>
        </div>
    );
};

export default SessionDetails;