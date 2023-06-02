import { render, screen } from '@testing-library/react';
import HeaderComponent from './HeaderComponent';

test('renders learn react link', () => {
  render(<HeaderComponent />);
  const headerName = screen.getByText("AI Code Inspector");
  expect(headerName).toBeInTheDocument();
});
