import { beforeEach, describe, expect, it } from 'vitest';
import { render, screen } from '@testing-library/react';
import App from './App';
import { session } from './api';

describe('routing', () => {
  beforeEach(() => {
    localStorage.clear();
    window.history.pushState({}, '', '/home');
  });

  it('redirects to the login page without a token', () => {
    render(<App />);

    expect(screen.getByRole('button', { name: 'Login' })).toBeInTheDocument();
  });

  it('shows the home page for a logged in user', async () => {
    session.save({ token: 't', username: 'alice', role: 'USER' });
    globalThis.fetch = async () => ({ ok: true, status: 200, json: async () => [] });

    render(<App />);

    expect(await screen.findByText('My events')).toBeInTheDocument();
    expect(screen.queryByText('New Event')).not.toBeInTheDocument();
  });
});
