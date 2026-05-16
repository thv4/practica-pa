import {useState, useEffect} from 'react';
import {useSelector} from 'react-redux';
import {useNavigate} from 'react-router';
import {FormattedMessage} from 'react-intl';
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';

import backend from '../../../backend';
import users from '../../users';

const PurchaseHistory = () => {

    const navigate = useNavigate();
    const isLoggedIn = useSelector(users.selectors.isLoggedIn);

    const [purchases, setPurchases] = useState([]);
    const [page,setPage] = useState(0);
    const [hasMore,setHasMore] = useState(false);

    useEffect(() => {
        if(!isLoggedIn){
            navigate('/users/login');
            return;
        }

        const load = async () => {
            const response = await backend.catalogService.getPurchaseHistory(page);
            if(response.ok) {
                setPurchases(response.payload.items);
                setHasMore(response.payload.existsMoreItems);
            }
        };

        load();
    } , [page,isLoggedIn]);

    return (
        <div>
            <h4><FormattedMessage id="project.catalog.PurchaseHistory.title"/></h4>

            {purchases.length === 0 ? (
                <p><FormattedMessage id="project.catalog.PurchaseHistory.empty"/></p>
            ) : (
                <Table striped bordered hover responsive>
                    <thead>
                    <tr>
                        <th><FormattedMessage id="project.catalog.PurchaseHistory.movie"/></th>
                        <th><FormattedMessage id="project.catalog.PurchaseHistory.session"/></th>
                        <th><FormattedMessage id="project.catalog.PurchaseHistory.sala"/></th>
                        <th><FormattedMessage id="project.catalog.PurchaseHistory.date"/></th>
                        <th><FormattedMessage id="project.catalog.PurchaseHistory.tickets"/></th>
                        <th><FormattedMessage id="project.catalog.PurchaseHistory.total"/></th>
                        <th><FormattedMessage id="project.catalog.PurchaseHistory.delivered"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    {purchases.map(p => (
                        <tr key={p.compraId}>
                            <td>{p.tituloPelicula}</td>
                            <td>{p.fechaHoraSesion}</td>
                            <td>{p.nombreSala}</td>
                            <td>{p.fechaRegistroCompra}</td>
                            <td>{p.numLocalidades}</td>
                            <td>{p.precioTotal} €</td>
                            <td>
                                {p.entregada
                                    ? <FormattedMessage id="project.catalog.PurchaseHistory.yes"/>
                                    : <FormattedMessage id="project.catalog.PurchaseHistory.no"/>
                                }
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </Table>
            )}

            <div className="d-flex gap-2 mt-2">
                <Button variant="secondary" disabled={page === 0}
                        onClick={() => setPage(p => p-1)}>
                    <FormattedMessage id="project.global.buttons.back"/>
                </Button>
                <Button variant="secondary" disabled={!hasMore}
                        onClick={() => setPage(p => p+1)}>
                    <FormattedMessage id="project.global.buttons.next"/>
                </Button>
            </div>
        </div>
    );
};
export default PurchaseHistory;