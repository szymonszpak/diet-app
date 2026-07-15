"use client";

import { useState, useEffect } from 'react';

export default function Home() {
    const [summary, setSummary] = useState(null);
    const [meals, setMeals] = useState([]);
    const [newMealName, setNewMealName] = useState('');

    const [searchQuery, setSearchQuery] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const [isSearching, setIsSearching] = useState(false);

    const [selectedMealId, setSelectedMealId] = useState('');
    const [weight, setWeight] = useState(100);

    const fetchData = () => {
        fetch('http://localhost:8080/api/meals/summary')
            .then(res => res.json())
            .then(data => setSummary(data))
            .catch(err => console.error(err));

        fetch('http://localhost:8080/api/meals')
            .then(res => res.json())
            .then(data => {
                setMeals(data);
                if(data.length > 0 && !selectedMealId) {
                    setSelectedMealId(data[0].id);
                }
            })
            .catch(err => console.error(err));
    };

    useEffect(() => {
        fetchData();
    }, []);

    const handleCreateMeal = (e) => {
        e.preventDefault();
        if (!newMealName.trim()) return;

        fetch(`http://localhost:8080/api/meals?name=${newMealName}`, {
            method: 'POST'
        })
            .then(() => {
                setNewMealName('');
                fetchData();
            })
            .catch(err => console.error(err));
    };

    const handleSearch = (e) => {
        e.preventDefault();
        if (!searchQuery.trim()) return;

        setIsSearching(true);
        setSearchResults([]);

        fetch(`http://localhost:8080/api/products/search?name=${searchQuery}`)
            .then(res => res.json())
            .then(data => {
                setSearchResults(data);
                setIsSearching(false);
            })
            .catch(err => {
                console.error(err);
                setIsSearching(false);
            });
    };

    const handleAddToMeal = async (product) => {
        if (!selectedMealId) {
            alert("Najpierw stwórz posiłek!");
            return;
        }

        try {
            let productId = product.id;

            if (!productId) {
                const addProductRes = await fetch('http://localhost:8080/api/products/add', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(product)
                });
                const savedProduct = await addProductRes.json();
                productId = savedProduct.id;
            }

            await fetch(`http://localhost:8080/api/meals/${selectedMealId}/products/${productId}?weight=${weight}`, {
                method: 'POST'
            });

            fetchData();

        } catch (err) {
            console.error(err);
        }
    };

    return (
        <div className="container">
            <h1 style={{ color: '#0d6efd', marginBottom: '20px' }}>Diet Builder - React</h1>

            {summary ? (
                <div className="card">
                    <h2>Podsumowanie Dnia</h2>
                    <p>Cel kaloryczny: <span style={{ color: '#0dcaf0' }}>{summary.calorieGoal} kcal</span></p>
                    <hr />
                    <h3>Spożyte kalorie: {summary.totalCalories} kcal</h3>
                    <p>
                        Białko: <strong>{summary.totalProtein}g</strong> |
                        Węgle: <strong>{summary.totalCarbs}g</strong> |
                        Tłuszcz: <strong>{summary.totalFat}g</strong>
                    </p>
                </div>
            ) : (
                <p>Ładowanie danych z backendu...</p>
            )}

            <div className="card">
                <h2 style={{ marginBottom: '15px' }}>Twoje Posiłki</h2>
                <form onSubmit={handleCreateMeal} style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
                    <input
                        type="text"
                        value={newMealName}
                        onChange={(e) => setNewMealName(e.target.value)}
                        placeholder="Nazwa posiłku (np. Śniadanie)"
                        style={{ flex: 1, padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }}
                    />
                    <button
                        type="submit"
                        style={{ padding: '10px 20px', backgroundColor: '#0d6efd', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                    >
                        Stwórz posiłek
                    </button>
                </form>

                {meals.length > 0 ? (
                    <ul style={{ listStyle: 'none', padding: 0 }}>
                        {meals.map(meal => {
                            const mealItemsList = meal.mealItems || meal.items || meal.products || [];
                            return (
                                <li key={meal.id} style={{ padding: '15px', borderBottom: '1px solid #eee', backgroundColor: '#fdfdfd', borderRadius: '6px', marginBottom: '10px' }}>
                                    <div style={{ marginBottom: '10px' }}>
                                        <strong style={{ fontSize: '1.2em' }}>{meal.name}</strong>
                                        <span style={{ float: 'right', color: '#198754', fontWeight: 'bold' }}>{meal.totalCalories} kcal</span>
                                    </div>

                                    {mealItemsList.length > 0 ? (
                                        <ul style={{ listStyle: 'circle', paddingLeft: '20px', margin: 0, color: '#555', fontSize: '0.95em' }}>
                                            {mealItemsList.map(item => (
                                                <li key={item.id} style={{ marginBottom: '5px' }}>
                                                    <strong>{item.product.name}</strong> ({item.weightInGrams}g) - {item.calculatedCalories} kcal
                                                </li>
                                            ))}
                                        </ul>
                                    ) : (
                                        <div style={{ color: '#999', fontSize: '0.9em', fontStyle: 'italic' }}>Brak produktów w posiłku</div>
                                    )}
                                </li>
                            );
                        })}
                    </ul>
                ) : (
                    <p style={{ color: '#666' }}>Brak posiłków. Stwórz pierwszy posiłek!</p>
                )}
            </div>

            <div className="card">
                <h2 style={{ marginBottom: '15px' }}>Znajdź produkt</h2>

                <form onSubmit={handleSearch} style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
                    <input
                        type="text"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        placeholder="Wpisz nazwę (np. jabłko, twaróg)..."
                        style={{ flex: 1, padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }}
                    />
                    <button
                        type="submit"
                        disabled={isSearching}
                        style={{ padding: '10px 20px', backgroundColor: '#198754', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                    >
                        {isSearching ? 'Szukam...' : 'Szukaj'}
                    </button>
                </form>

                {searchResults.length > 0 && (
                    <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
                        {searchResults.map((product, index) => (
                            <li key={index} style={{ borderBottom: '1px solid #eee', padding: '15px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '10px' }}>
                                <div style={{ flex: '1 1 100%' }}>
                                    <strong style={{ fontSize: '1.1em' }}>{product.name}</strong>
                                    <div style={{ fontSize: '0.9em', color: '#666', marginTop: '5px' }}>
                                        {product.calories} kcal/100g (B: {product.protein}g, W: {product.carbs}g, T: {product.fat}g)
                                    </div>
                                </div>

                                <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                                    <input
                                        type="number"
                                        value={weight}
                                        onChange={e => setWeight(e.target.value)}
                                        style={{ width: '80px', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                                    />
                                    <span style={{ color: '#666' }}>g</span>

                                    <select
                                        value={selectedMealId}
                                        onChange={e => setSelectedMealId(e.target.value)}
                                        style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                                    >
                                        <option value="" disabled>Wybierz posiłek</option>
                                        {meals.map(m => <option key={m.id} value={m.id}>{m.name}</option>)}
                                    </select>

                                    <button
                                        onClick={() => handleAddToMeal(product)}
                                        style={{ backgroundColor: '#0d6efd', color: 'white', border: 'none', padding: '8px 16px', borderRadius: '4px', cursor: 'pointer' }}
                                    >
                                        Dodaj do posiłku
                                    </button>
                                </div>
                            </li>
                        ))}
                    </ul>
                )}

                {searchResults.length === 0 && searchQuery && !isSearching && (
                    <p style={{ color: '#666' }}>Brak wyników lub wpisz coś, żeby wyszukać.</p>
                )}
            </div>
        </div>
    );
}