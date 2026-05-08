import {useSelector, useDispatch} from 'react-redux';
import Movies from './Movies';
import * as selectors from '../selectors';
import DateSelector from "./DateSelector";
import backend from '../../../backend';
import * as actions from '../actions';

const Billboard = () => {
    const movies = useSelector(selectors.getMovies);
    const billboardDate = useSelector(selectors.getBillboardDate);
    const dispatch = useDispatch();

    const handleBillboardDateChange = async date => {
        dispatch(catalog.actions.clearBillboard(date));
        const response = await backend.catalogService.getBillboard(date);
        if (response.ok) {
            dispatch(actions.getBillboardCompleted(response.payload));
        }
    }

    return (
        <div>
            <DateSelector id="billboardDate" className="mb-2 w-auto"
                          value={billboardDate} onChange={e => handleBillboardDateChange(e.target.value)} />
            <Movies movies={movies}/>
        </div>
    );

}
export default Billboard;