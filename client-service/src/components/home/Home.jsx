import React, { useCallback, useEffect, useState } from 'react';
import { api, session } from '../../api';
import './Home.css';

const emptyForm = { title: '', topic: '', content: '', dueDate: '', userId: '' };

const formatDate = (iso) => (iso ? iso.replace('T', ' ').slice(0, 16) : '-');

const Home = () => {
  const isAdmin = session.isAdmin;

  const [events, setEvents] = useState([]);
  const [users, setUsers] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState(emptyForm);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  const loadEvents = useCallback(async () => {
    try {
      setEvents(await api('/events'));
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadEvents();
    if (isAdmin) {
      api('/users')
        .then(setUsers)
        .catch((e) => setError(e.message));
    }
  }, [isAdmin, loadEvents]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await api('/events', {
        method: 'POST',
        body: { ...formData, userId: Number(formData.userId) },
      });
      setFormData(emptyForm);
      setShowForm(false);
      await loadEvents();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this event?')) return;
    setError('');
    try {
      await api(`/events/${id}`, { method: 'DELETE' });
      setEvents((prev) => prev.filter((ev) => ev.id !== id));
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="container">
      {error && <p className="error-banner" role="alert">{error}</p>}

      {isAdmin && (
        <div className="toolbar">
          {!showForm && (
            <button className="button" onClick={() => setShowForm(true)}>
              New Event
            </button>
          )}
        </div>
      )}

      {isAdmin && showForm && (
        <form className="task-form" onSubmit={handleSubmit}>
          <div className="form-field">
            <label htmlFor="title">Title</label>
            <input id="title" type="text" name="title" value={formData.title} onChange={handleChange} required />
          </div>

          <div className="form-field">
            <label htmlFor="topic">Topic</label>
            <input id="topic" type="text" name="topic" value={formData.topic} onChange={handleChange} />
          </div>

          <div className="form-field">
            <label htmlFor="content">Content</label>
            <textarea id="content" name="content" value={formData.content} onChange={handleChange} />
          </div>

          <div className="form-field">
            <label htmlFor="dueDate">Due Date</label>
            <input id="dueDate" type="datetime-local" name="dueDate" value={formData.dueDate} onChange={handleChange} required />
          </div>

          <div className="form-field-user">
            <label htmlFor="userId">User</label>
            <select id="userId" name="userId" value={formData.userId} onChange={handleChange} required>
              <option value="">Choose user</option>
              {users.map((user) => (
                <option key={user.id} value={user.id}>
                  {user.username}
                </option>
              ))}
            </select>
          </div>

          <div className="form-actions">
            <button type="submit" className="submit-button">Submit</button>
            <button type="button" className="cancel-button" onClick={() => setShowForm(false)}>Cancel</button>
          </div>
        </form>
      )}

      <section className="events">
        <h2>{isAdmin ? 'All events' : 'My events'}</h2>
        {loading ? (
          <p>Loading...</p>
        ) : events.length === 0 ? (
          <p>No events yet.</p>
        ) : (
          <ul className="event-list">
            {events.map((ev) => (
              <li key={ev.id} className="event-card">
                <div>
                  <strong>{ev.title}</strong>
                  {ev.topic && <span className="event-topic">{ev.topic}</span>}
                  <p>{ev.content}</p>
                  <small>
                    {formatDate(ev.dueDate)}
                    {isAdmin && ev.username && ` · for ${ev.username}`}
                  </small>
                </div>
                <button type="button" className="delete-button" onClick={() => handleDelete(ev.id)}>
                  Delete
                </button>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  );
};

export default Home;
