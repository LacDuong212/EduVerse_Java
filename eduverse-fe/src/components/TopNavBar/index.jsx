import clsx from 'clsx';
import { useRef } from 'react';


const TopNavbar = ({ children, className }) => {
  const headerRef = useRef(null);

  return (
    <header
      ref={headerRef}
      className={clsx(
        'navbar-light sticky-top',
        className
      )}
      style={{
        zIndex: 99,
      }}
    >
      <nav className="navbar navbar-expand-md py-2 transition-all">
        {children}
      </nav>
    </header>
  );
};

export default TopNavbar;
