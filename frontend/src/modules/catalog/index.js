import * as actions from './actions';
import reducer from './reducer'; // Importamos el reducer
import * as selectors from './selectors';
import { default as Billboard } from './components/Billboard';
import { default as SessionDetails } from './components/SessionDetails';

export default {actions, reducer, selectors};

export { default as Billboard } from './components/Billboard';

export { default as SessionDetails } from './components/SessionDetails';

export { default as BuyTickets} from './components/BuyTickets.jsx';

export { default as BuyConfirm} from './components/BuyConfirm.jsx';

export { default as PurchaseHistory} from './components/PurchaseHistory';