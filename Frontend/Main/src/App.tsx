import { Spinner } from '@fluentui/react-components'
import './App.css'
//import { useAuth } from './contexts/authContext';
//import { Search } from './components/search/Search';

function App() {

  /*const {token} = useAuth();

  const isLoggedIn = token === null;
  console.log(isLoggedIn);
*/
  return (
    <>
      <Spinner size='large' label='Loading...' labelPosition='after' />
    </>
  )
}

export default App
