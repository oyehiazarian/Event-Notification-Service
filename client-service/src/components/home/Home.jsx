import React, { useState } from 'react';
import './Home.css';  // Подключаем CSS файл

const Home = () => {
  const [showForm, setShowForm] = useState(false);  // Стейт для отображения формы
  const [formData, setFormData] = useState({
    title: '',
    topic: '',
    content: '',
    due_date: '',
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {

    const token = localStorage.getItem("token"); // Извлекаем токен из localStorage

    if (!token) {
      alert("Вы не авторизованы!");
      return;
    }
    e.preventDefault();
    fetch('http://localhost:8080/new_event', {
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
        due_date: "",
        userId: {}
       
      }),
    })  
      .then((data) => {
        alert('New task added');
        setShowForm(false);  // Скрыть форму после отправки
        setFormData({
          title: '',
          topic: '',
          content: '',
          due_date: '',
        }); // Очистить поля
      })
      .catch((error) => {
        console.error('Error:', error);


        alert('Failed to add new task');
      });
    }

  return (
    <div className="container">
      {!showForm ? (
        <button className="button" onClick={() => setShowForm(true)}>
          New Task
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
              type="date"
              name="due_date"
              value={formData.due_date}
              onChange={handleChange}
              
            />
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
