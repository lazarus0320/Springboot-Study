import { fireEvent, render, screen } from '@testing-library/react';
import App from './App';
import AddCar from './components/AddCar';
import TestRenderer from 'react-test-renderer';

// test('renders learn react link', () => {
//   render(<App />);
//   const linkElement = screen.getByText(/learn react/i);
//   expect(linkElement).toBeInTheDocument();
// });

test('open add car modal form', async () => {
  render(<App />);
  fireEvent.click(screen.getByText('New Car'));
  expect(screen.getByRole('dialog')).toHaveTextContent('New car');
});

test('renders a snapshot', () => {
  const tree = TestRenderer.create(<AddCar />).toJSON();
  expect(tree).toMatchSnapshot();
});
