import { useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import BookingOptions from '../../components/public/BookingOptions';
import HomeContent from '../../components/public/HomeContent';
import HomeHero from '../../components/public/HomeHero';
import HomeHighlights from '../../components/public/HomeHighlights';
import { AppContext } from '../../context/AppContext';
import {
  BOOKING_OPTIONS,
  CLINIC_HOURS,
  HOME_HIGHLIGHTS,
  HOME_SERVICES,
} from '../../data/homePageData';
import { ROLE_CONFIG } from '../../data/roleConfig';

export default function HomePage() {
  const navigate = useNavigate();
  const { currentRole, isLogged } = useContext(AppContext);

  const dashboardPath = ROLE_CONFIG[currentRole]?.dashboard ?? '/login';

  function handleLoginNavigation() {
    navigate('/login');
  }

  return (
    <div className="homepage" id="homepage">
      <HomeHero
        currentRole={currentRole}
        dashboardPath={dashboardPath}
        isLogged={isLogged}
        onNavigate={navigate}
      />
      <HomeHighlights highlights={HOME_HIGHLIGHTS} />
      <HomeContent clinicHours={CLINIC_HOURS} services={HOME_SERVICES} />
      <BookingOptions
        options={BOOKING_OPTIONS}
        onLogin={handleLoginNavigation}
        onNavigate={navigate}
      />
    </div>
  );
}
