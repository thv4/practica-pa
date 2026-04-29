import {useSelector} from 'react-redux';
import Movies from './Movies';
import * as selectors from '../selectors';

const Billboard = () => {
    const movies = useSelector(selectors.getMovies);

    return (
        <Movies movies={movies}/>
    );

}

export default Billboard;