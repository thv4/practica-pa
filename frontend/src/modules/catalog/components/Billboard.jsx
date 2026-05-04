import {useSelector} from 'react-redux';
import Movies from './Movies';
import * as selectors from '../selectors';
import DateSelector from "./DateSelector";

const Billboard = () => {
    const movies = useSelector(selectors.getMovies);

    return (
        <div>
            <DateSelector id="billboardDate" className="mb-2 w-auto"/>
            <Movies movies={movies}/>
        </div>
    );

}
export default Billboard;

export default Billboard;