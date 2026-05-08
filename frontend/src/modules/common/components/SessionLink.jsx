import {Link} from 'react-router'

const SessionLink = ({id, name}) => {
    return(
        <Link to={`/catalog/session-details/${id}`}>
            {name}
        </Link>
    );
}

export default SessionLink;