import { Outlet } from 'react-router-dom';
import Header from '../components/Header';
import Sidebar from '../components/Sidebar';
import HelpAction from '../components/HelpAction';

export default function AppLayout() {
  return (
    <>
      <Header />
      <main className="app-container">
        <Sidebar />
        <div className="content-wrapper">
          <Outlet />
        </div>
      </main>
      <HelpAction />
    </>
  );
}
