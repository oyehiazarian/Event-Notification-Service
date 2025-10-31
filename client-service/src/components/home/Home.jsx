import React, { useState, useEffect } from 'react';

import './Home.css';  

const Home = () => {
  const [users, setUsers] = useState([]);             
  const [loadingUsers, setLoadingUsers] = useState(false); 
  const [showForm, setShowForm] = useState(false);    
  const [formData, setFormData] = useState({
    title: '',
    topic: '',
    content: '',
    due_date: '',
    userId: '', 
  });
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      alert("You are not authorized");
      return;
    }
  
    setLoadingUsers(true);
  
    fetch("http://localhost:8090/all-users", {
      method: "GET", 
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
      },
    })
      .then((res) => res.text())
      .then((text) => {
        const data = JSON.parse(text);
        const userArray = Object.entries(data).map(([id, username]) => ({
          id,
          username,
        }));
        setUsers(userArray);
        setLoadingUsers(false);
      })
      .catch((err) => {
        console.error("Error during getting all users:", err);
        setLoadingUsers(false);
      });
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {

    const token = localStorage.getItem("token");

    if (!token) {
      alert("You are not authorized");
      return;
    }
    e.preventDefault();
    fetch('http://localhost:8090/new_event', {
      method: 'POST',
    
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify({
        id: 0,
        title: formData.title,
        topic: formData.topic,
        content: formData.content,
        due_date: formData.due_date,
        userId: formData.userId
       
      }),
    })
    .then((response) => {
      if (!response.ok) {
        const error = new Error('Server error');
        error.status = response.status;
        throw error;
      }
      return response.json(); 
    })  
      .then((data) => {
        alert('New task added');
        setShowForm(false);  
        setFormData({
          title: '',
          topic: '',
          content: '',
          due_date: "",
        }); 
      })
      .catch((error) => {
        console.error('Error status:', error.status); 
        alert(`Error: ${error.status}`);
      });
    }

  

    return (
      <div className="container">
        {!showForm ? (
          <button className="button" onClick={() => setShowForm(true)}>
            New Event
          </button>
        ) : (
          <form className="task-form" onSubmit={handleSubmit}>
            <div className="form-field">
              <label>Title</label>
              <input
                type="text"
                name="title"
                value={formData.title}
                onChange={handleChange}
                required
              />
            </div>
  
            <div className="form-field">
              <label>Topic</label>
              <input
                type="text"
                name="topic"
                value={formData.topic}
                onChange={handleChange}
                required
              />
            </div>
  
            <div className="form-field">
              <label>Content</label>
              <textarea
                name="content"
                value={formData.content}
                onChange={handleChange}
                required
              ></textarea>
            </div>
  
            <div className="form-field">
              <label>Due Date</label>
              <input
                type="datetime-local"
                name="due_date"
                value={formData.due_date}
                onChange={handleChange}
              />
            </div>
  
            {/*Выбор пользователя */}
            <div className="form-field-user">
              <label>User</label>
              <select
                name="userId"
                value={formData.userId}
                onChange={handleChange}
                required
              >
                <option value="">Chose user</option>
                {loadingUsers ? (
                  <option disabled>Downloading...</option>
                ) : (
                  users.map((user) => (
                    <option key={user.id} value={user.id}>
                      {user.username}
                    </option>
                  ))
                )}
              </select>
            </div>
  
            <button type="submit" className="submit-button">
              Submit
            </button>
          </form>
        )}
      </div>
    );
  };
  
  export default Home;