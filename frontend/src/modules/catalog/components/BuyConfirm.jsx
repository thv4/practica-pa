import {useSelector} from 'react-redux';
import {useNavigate} from 'react-router';
import {FormattedMessage} from 'react-intl';
import Alert from 'react-bootstrap/Alert';
import Button from 'react-bootstrap/Button';

import * as selectors from '../selectors';

const BuyConfirm = () => {

    const navigate = useNavigate();
    const compraId = useSelector(selectors.getLastPurchaseId);

    if(!compraId){
        navigate('/');
        return null;
    }

    return (
        <div className="col-md-8 mx-auto mt-4">
            <Alert variant="success">
                <Alert.Heading>
                    <FormattedMessage id="project.catalog.BuyConfirm.title"/>
                </Alert.Heading>
                <p>
                    <FormattedMessage
                        id="project.catalog.BuyConfirm.message"
                        values={{compraId}}/>
                </p>
                <hr/>
                <div className="d-flex gap-2">
                    <Button variant="primary" onClick={() => navigate('/')}>
                        <FormattedMessage id="project.app.Header.home"/>
                    </Button>
                </div>
            </Alert>
        </div>
    );
};

export default BuyConfirm;