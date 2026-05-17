import {useState} from 'react';
import {useDispatch} from 'react-redux';
import {useNavigate, useParams} from 'react-router';
import {FormattedMessage} from 'react-intl';
import Card from 'react-bootstrap/Card';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

import {Errors} from '../../common';
import * as actions from '../actions';
import backend from '../../../backend';
import users from '../../users';
import {useSelector} from 'react-redux';


const BuyTickets = () => {

    const dispatch = useDispatch();
    const navigate = useNavigate();
    const {sesionId} = useParams();

    const isLoggedIn = useSelector(users.selectors.isLoggedIn);

    const [numLocalidades,setNumLocalidades] = useState(1);
    const [tarjetaBancaria,setTarjetaBancaria] = useState('');
    const [formValidated,setFormValidated] = useState(false);
    const [backendErrors, setBackendErrors] = useState(null);
    const user = useSelector(users.selectors.getUser);

    let form;

    if(!isLoggedIn || (user && user.role !== 'ESPECTADOR')){
        navigate('/users/login');
        return null;
    }

    const handleSubmit = async event => {

        event.preventDefault();

        if(form.checkValidity()) {

            const response = await backend.catalogService.buyTickets(
                Number(sesionId),
                Number(numLocalidades),
                tarjetaBancaria,
                () => {
                    navigate('/users/login');
                    dispatch(users.actions.logout());
                }
                );
            if(response.ok){
                dispatch(actions.buyTicketsCompleted(response.payload));
                navigate('/catalog/buy-confirm');
            }else{
                setBackendErrors(response.payload);
            }
        }else{
            setBackendErrors(null);
            setFormValidated(true);
        }
    };

    return (
        <div className="col-md-10 mx-auto">
            <Errors errors={backendErrors} onClose={() => setBackendErrors(null)}/>
            <Card className="bg-light border-dark">
                <Card.Header as="h5">
                    <FormattedMessage id="project.catalog.BuyTickets.title"/>
                </Card.Header>
                <Card.Body>
                    <Form ref={node => form= node}
                          noValidate validated={formValidated} onSubmit={e => handleSubmit(e)}>
                        <Form.Group as={Row} className="mb-3" controlId="numLocalidades">
                            <Form.Label column md={3}>
                                <FormattedMessage id="project.catalog.BuyTickets.fields.numLocalidades"/>
                            </Form.Label>
                            <Col md={4}>
                                <Form.Control type="number"
                                    min="1"
                                    value={numLocalidades}
                                    onChange={e => setNumLocalidades(e.target.value)}
                                    required/>
                                <Form.Control.Feedback type="invalid">
                                    <FormattedMessage id="project.global.validator.required"/>
                                </Form.Control.Feedback>
                            </Col>
                        </Form.Group>

                        <Form.Group as={Row} className="mb-3" controlId="tarjetaBancaria">
                            <Form.Label column md={3}>
                                <FormattedMessage id="project.catalog.BuyTickets.fields.tarjetaBancaria"/>
                            </Form.Label>
                            <Col md={4}>
                                <Form.Control type="text"
                                    value={tarjetaBancaria}
                                    onChange={e => setTarjetaBancaria(e.target.value)}
                                    minLength={16}
                                    maxLength={16}
                                    placeholder="1234567890123456"
                                    required/>
                                <Form.Control.Feedback type="invalid">
                                    <FormattedMessage id="project.catalog.BuyTickets.validator.tarjetaBancaria"/>
                                </Form.Control.Feedback>
                            </Col>
                        </Form.Group>

                        <Form.Group as={Row}>
                            <Col md={{span: 4,offset: 3}} className="d-flex gap-2">
                                <Button type="submit">
                                    <FormattedMessage id="project.catalog.BuyTickets.buttons.buy"/>
                                </Button>
                                <Button variant="secondary" onClick={() => navigate(-1)}>
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

export default BuyTickets;