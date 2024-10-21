import './App.css'
//import { useAuth } from './contexts/authContext';
import { Search } from './components/search/Search';

function App() {

  /*const {token} = useAuth();

  const isLoggedIn = token === null;
  console.log(isLoggedIn);
*/
  return (
    <>
      <Search />
    </>
  )
}

export default App
