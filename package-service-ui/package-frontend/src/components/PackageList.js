import React, { useEffect, useState } from "react";
import { getPackages, deletePackage } from "../api";

function PackageList() {
	const [packages, setPackages] = useState([]);
	const [error, setError] = useState(null);
	const [currency, setCurrency] = useState("");
	
	async function loadPackages(selectedCurrency) {
		try {
			const data = await getPackages(selectedCurrency);
			setPackages(data);
		} 
		catch (err) {
			setError(err.message);
		}
	}
	
	useEffect(() => {
		loadPackages();
	}, []);
	
	async function handleDelete(id) {
		try {
			await deletePackage(id);
			loadPackages();
		} catch (err) {
			setError(err.message);
		}
	}
	
	function handleRefresh() {
		loadPackages(currency);
	}
	
	return (
		<div>
			<h2>Packages</h2>
		
			<div style={{ marginBottom: "20px" }}>
				<input
					type="text"
					placeholder="(USD, EUR, GBP, JPY etc.)"
					value={currency}
					onChange={(e) => setCurrency(e.target.value.toUpperCase())}
					style={{ marginRight: "10px" }}
				/>
				<button className="primary" onClick={handleRefresh}>Refresh</button>
			</div>
		
			{error && <p style={{ color: "red" }}>{error}</p>}
		
			{packages.length === 0 && (
				<p style={{ color: "#666" }}>No packages found.</p>
			)}
		
			{packages.map((pkg) => (
			<div key={pkg.id} className="package-item">
				<div className="package-info">
					<div>
						<strong>{pkg.name}</strong>
					</div>
					<div>
						Description: {pkg.description}
					</div>
					<div style={{ marginTop: "10px" }}>
						Products:
						<ul>
							{pkg.products.map((product) => (
								<li key={product.id}>
								{product.name}: ${product.usdPrice.toFixed(2)}
								</li>
							))}
						</ul>
					</div>
					<div>
						Total Price: {pkg.totalPrice} {pkg.currency}
					</div>
				</div>
		
				<button
				className="danger"
				onClick={() => handleDelete(pkg.id)}
				>
				Delete
				</button>
			</div>
			))}
		</div>
	);
}

export default PackageList;