import React, { useState } from "react";
import { createPackage } from "../api";

function CreatePackageForm({ onCreated }) {
	const [name, setName] = useState("");
	const [description, setDescription] = useState("");
	const [productIds, setProductIds] = useState("");
	
	async function handleSubmit(e) {
		e.preventDefault();
	
		const payload = {
			name,
			description,
			productIds: productIds.split(",").map(id => id.trim())
		};
	
		try {
			await createPackage(payload);
			setName("");
			setDescription("");
			setProductIds("");
			onCreated();
		} 
		catch (err) {
			alert(err.message);
		}
	}
	
	return (
		<form onSubmit={handleSubmit}>
			<h2>Create Package</h2>
			
			<div className="form-group">
				<input
				placeholder="Name"
				value={name}
				onChange={(e) => setName(e.target.value)}
				required
				/>
			</div>
			
			<div className="form-group">
				<input
				placeholder="Description"
				value={description}
				onChange={(e) => setDescription(e.target.value)}
				required
				/>
			</div>
			
			<div className="form-group">
				<input
				placeholder="Product IDs (comma separated)"
				value={productIds}
				onChange={(e) => setProductIds(e.target.value)}
				required
				/>
			</div>
			
			<button type="submit" className="primary">
				Create Package
			</button>
		</form>
	);
}

export default CreatePackageForm;