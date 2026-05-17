import {useState} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {useNavigate} from 'react-router';
import {FormattedMessage} from 'react-intl';
import Card from 'react-bootstrap/Card';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Alert from 'react-bootstrap/Alert';

import {BackLink, Errors} from '../../common';
import * as actions from '../actions';
import backend from '../../../backend';
import users from '../../users';

const DeliverTickets = () => {

    const dispatch = useDispatch();
    const navigate = useNavigate();

    const isLoggedIn = useSelector(users.selectors.isLoggedIn);
    const user = useSelector(users.selectors.getUser);

    const [compraId, setCompraId] = useState('');
    const [tarjetaBancaria, setTarjetaBancaria] = useState('');
    const [formValidated, setFormValidated] = useState(false);
    const [backendErrors, setBackendErrors] = useState(null);
    const [success, setSuccess] = useState(false);

    let form;

    if (!isLoggedIn || (user && user.role !== 'TAQUILLERO')) {
        navigate('/users/login');
        return null;
    }

    const handleSubmit = async event => {
        event.preventDefault();
        setSuccess(false);

        if (form.checkValidity()) {
            const response = await backend.catalogService.deliverTickets(
                Number(compraId),
                tarjetaBancaria,
                () => {
                    dispatch(users.actions.logout());
                    navigate('/users/login');
                }
            );

            if (response.ok) {
                dispatch(actions.deliverTicketsCompleted(response.payload));

                setSuccess(true);
                setCompraId('');
                setTarjetaBancaria('');
                setBackendErrors(null);
                setFormValidated(false);
            } else {
                setBackendErrors(response.payload);
            }
        } else {
            setBackendErrors(null);
            setFormValidated(true);
        }
    };

    return (
        <div className="col-md-10 mx-auto mt-4">
            <Errors errors={backendErrors} onClose={() => setBackendErrors(null)}/>

            {success && (
                <Alert variant="success" dismissible onClose={() => setSuccess(false)}>
                    <FormattedMessage id="project.catalog.DeliverTickets.success"/>
                </Alert>
            )}

            <div className="mb-3">
                <BackLink />
            </div>

            <Card className="bg-light border-dark shadow-sm">
                <Card.Header as="h5" className="bg-dark text-white py-3">
                    <FormattedMessage id="project.catalog.DeliverTickets.title"/>
                </Card.Header>
                <Card.Body className="p-4">
                    <Form ref={node => form = node}
                          noValidate validated={formValidated} onSubmit={e => handleSubmit(e)}>

                        <Form.Group as={Row} className="mb-4" controlId="compraId">
                            <Form.Label column md={3} className="fw-bold">
                                <FormattedMessage id="project.catalog.DeliverTickets.fields.compraId"/>
                            </Form.Label>
                            <Col md={6}>
                                <Form.Control
                                    type="text"
                                    value={compraId}
                                    onChange={e => setCompraId(e.target.value)}
                                    required
                                />
                                <Form.Control.Feedback type="invalid">
                                    <FormattedMessage id="project.global.validator.required"/>
                                </Form.Control.Feedback>
                            </Col>
                        </Form.Group>

                        <Form.Group as={Row} className="mb-4" controlId="tarjetaBancaria">
                            <Form.Label column md={3} className="fw-bold">
                                <FormattedMessage id="project.catalog.DeliverTickets.fields.tarjetaBancaria"/>
                            </Form.Label>
                            <Col md={6}>
                                <Form.Control
                                    type="text"
                                    value={tarjetaBancaria}
                                    onChange={e => setTarjetaBancaria(e.target.value)}
                                    minLength={16}
                                    maxLength={16}
                                    required
                                />
                                <Form.Control.Feedback type="invalid">
                                    <FormattedMessage id="project.catalog.BuyTickets.validator.tarjetaBancaria"/>
                                </Form.Control.Feedback>
                            </Col>
                        </Form.Group>

                        <Form.Group as={Row}>
                            <Col md={{span: 6, offset: 3}} className="d-flex gap-3">
                                <Button type="submit" variant="primary" className="px-4">
                                    <FormattedMessage id="project.catalog.DeliverTickets.buttons.deliver"/>
                                </Button>
                                <Button variant="outline-secondary" onClick={() => navigate(-1)}>
                                    <FormattedMessage id="project.global.buttons.cancel"/>
                                </Button>
                            </Col>
                        </Form.Group>

                    </Form>
                </Card.Body>
            </Card>
        </div>
    );
};

export default DeliverTickets;