import { API_CONFIG } from "./config";

const BASE_URL = API_CONFIG.BASE_URL;

export async function getPackages(currency) {
	const url = currency ? `${BASE_URL}?currency=${currency}` : BASE_URL;
	const response = await fetch(url);
	
	if (!response.ok) 
		throw new Error("Failed to fetch packages");
	
	return response.json();
}

export async function createPackage(pkg) {
	const response = await fetch(BASE_URL, {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify(pkg),
	});

  if (!response.ok) 
	  throw new Error("Failed to create package");
  
  return response.json();
}

export async function deletePackage(id) {
  const response = await fetch(`${BASE_URL}/${id}`, {
    method: "DELETE",
  });

  if (!response.ok) throw new Error("Failed to delete package");
}