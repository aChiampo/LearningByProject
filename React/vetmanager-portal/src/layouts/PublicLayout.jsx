import { Outlet } from 'react-router-dom';
import Header from '../components/Header';

export default function PublicLayout() {
  return (
    <>
      <Header />
      <main className="app-container app-container--public">
        <div className="content-wrapper content-wrapper--public">
          <Outlet />
        </div>
      </main>
    </>
  );
}
